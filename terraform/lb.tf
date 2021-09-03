data "aws_lb" "main" {
  name = var.external_lb_name
}

resource "aws_route53_record" "planning_bot" {
  zone_id = aws_route53_zone.planning_bot.id
  name    = var.domain
  type    = "A"

  alias {
    name                   = data.aws_lb.main.dns_name
    zone_id                = data.aws_lb.main.zone_id
    evaluate_target_health = true
  }
}

data "aws_lb_listener" "https" {
  load_balancer_arn = data.aws_lb.main.arn
  port              = 443
}

resource "aws_lb_listener_certificate" "backend" {
  listener_arn    = data.aws_lb_listener.https.arn
  certificate_arn = aws_acm_certificate.planning_bot.arn
}

resource "aws_acm_certificate" "planning_bot" {
  domain_name       = var.domain
  validation_method = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_route53_record" "planning_bot_certificate" {
  for_each = {
    for dvo in aws_acm_certificate.planning_bot.domain_validation_options : dvo.domain_name => {
      name   = dvo.resource_record_name
      record = dvo.resource_record_value
      type   = dvo.resource_record_type
    }
  }

  allow_overwrite = true
  name            = each.value.name
  records         = [each.value.record]
  ttl             = 60
  type            = each.value.type
  zone_id         = aws_route53_zone.planning_bot.zone_id
}

resource "aws_acm_certificate_validation" "planning_bot" {
  certificate_arn         = aws_acm_certificate.planning_bot.arn
  validation_record_fqdns = [for record in aws_route53_record.planning_bot_certificate : record.fqdn]
}


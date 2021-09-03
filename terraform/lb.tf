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

resource "aws_wafv2_web_acl" "planning_bot" {
  name        = "planning-bot-gateway-acl"
  description = "Web ACL that prevents from accessing Planning Bot from outside the office"
  scope       = "REGIONAL"

  default_action {
    allow {}
  }

  rule {
    name     = "block-non-office"
    priority = 1

    action {
      block {}
    }

    statement {
      and_statement {
        statement {
          regex_pattern_set_reference_statement {
            arn = aws_wafv2_regex_pattern_set.planning_bot_host.arn

            text_transformation {
              priority = 1
              type     = "LOWERCASE"
            }

            field_to_match {
              single_header {
                name = "host"
              }
            }
          }
        }

        statement {
          not_statement {
            statement {
              ip_set_reference_statement {
                arn = aws_wafv2_ip_set.office_ips.arn
              }
            }
          }
        }
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = false
      metric_name                = "planning-bot-outside-office"
      sampled_requests_enabled   = false
    }
  }

  visibility_config {
    cloudwatch_metrics_enabled = false
    metric_name                = "planning-bot-gateway-acl"
    sampled_requests_enabled   = false
  }
}

resource "aws_wafv2_ip_set" "office_ips" {
  name               = "PlanningBotIPWhitelist"
  description        = "Whitelist for Plannig Bot to allows access from office"
  scope              = "REGIONAL"
  ip_address_version = "IPV4"

  addresses = [
    "${var.office_ip_address}/32",
  ]
}

resource "aws_wafv2_regex_pattern_set" "planning_bot_host" {
  name        = "PlanningBotHost"
  description = "Planning Bot host regexp"
  scope       = "REGIONAL"

  regular_expression {
    regex_string = "^${var.domain}$"
  }
}

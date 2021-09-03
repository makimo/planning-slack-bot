variable "aws_region" {
  default = "eu-west-1"
}

variable "datacenter" {
  default = "makimo1"
}

variable "domain" {
  default = "fistaszek.makimo.pl"
}

variable "external_lb_name" {
  default = "hs-external-lb"
}

variable "common_tags" {
  default = {
    Project : "PB",
    Repository : "planning-slack-bot",
  }
}

variable "aws_region" {
  default = "eu-west-1"
}

variable "common_tags" {
  default = {
    Project : "PB",
    Repository : "planning-slack-bot",
  }
}

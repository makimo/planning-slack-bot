provider "aws" {
  region = var.aws_region

  default_tags {
    tags = var.common_tags
  }
}

terraform {
  backend "s3" {
    bucket = "makimo-deployments"
    key    = "planning-slack-bot"
    region = "eu-west-1"
  }
}


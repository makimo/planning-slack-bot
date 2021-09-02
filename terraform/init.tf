provider "aws" {
  region = var.aws_region

  default_tags {
    tags = var.common_tags
  }
}

provider "consul" {
  address    = "hs.makimo.pl:8501"
  scheme     = "https"
  datacenter = var.datacenter

  ca_file   = "ssl/consul-agent-ca.pem"
  cert_file = "ssl/client.consul.crt"
  key_file  = "ssl/client.consul.key"
}

provider "nomad" {
  address = "https://hs.makimo.pl:4646"

  ca_file   = "ssl/nomad-agent-ca.pem"
  cert_file = "ssl/client.nomad.crt"
  key_file  = "ssl/client.nomad.key"
}

terraform {
  backend "s3" {
    bucket = "makimo-deployments"
    key    = "planning-slack-bot"
    region = "eu-west-1"
  }
}

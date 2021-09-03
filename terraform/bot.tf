resource "nomad_job" "planning_bot" {
  jobspec = templatefile(
    "${path.module}/jobs/bot.nomad",
    {
      datacenters = [var.datacenter],
      image       = "${aws_ecr_repository.planning_bot.repository_url}:${data.consul_keys.bot_image.var.tag}"
      domain      = var.domain
    }
  )
}

data "consul_keys" "bot_image" {
  key {
    name = "tag"
    path = "internal/planning-bot/backend_tag"
  }
}

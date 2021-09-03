job "planning-bot" {
  datacenters = [%{ for datacenter in datacenters ~}"${datacenter}"%{ endfor ~}]
  type = "service"

  group "bot" {
    network {
      port "http" { to = 8080 }
    }

    task "bot" {
      driver = "docker"

      config {
        image = "${image}"
        ports = ["http"]
      }

      resources {
        cpu = 500
        memory = 512
      }
    }

    service {
      name = "planning-bot"
      port = "http"

      tags = [
        "urlprefix-${domain}/"
      ]
    }
  }
}

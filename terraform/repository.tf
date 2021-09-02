resource "aws_ecr_repository" "name_microservice" {
  name                 = "planning-bot"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}

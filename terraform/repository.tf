resource "aws_ecr_repository" "planning_bot" {
  name                 = "planning-bot"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}

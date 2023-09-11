output "ecr_public_repository_uri" {
  value = aws_ecrpublic_repository.ecr_repo.repository_uri
}
output "ecr_public_repository_name" {
  value = aws_ecrpublic_repository.ecr_repo.repository_name
}
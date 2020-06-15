@Library('jenkins-share-library@v1.0.0') _

def gitCommit = null
def gitBranch = null
def imageTag = null
def podLabel = "worker-${UUID.randomUUID().toString()}"
def project = 'k8spipeline'
def namespace = 'devops'
def currentEnv = 'dev'

pipeline {
  agent {
    kubernetes {
      cloud 'kubernetes-dev'
      label "${podLabel}"
      yaml libraryResource('template/kubernetesPod.yaml')
    }
  }
  options {
    skipDefaultCheckout()
  }
  stages {
    stage('Build & Deploy') {
      steps {
        script {
          stage('Checkout Code') {
            checkout scm
            gitCommit = getGitCommit()
            gitBranch = getGitBranch(scm)
            imageTag = "${env.BUILD_ID}_${gitCommit}"
          }
          javaCodeTest(
            gitCommit: gitCommit,
            gitBranch: gitBranch
          )
          javaBuildArtifacts()
          publishArtifacts(
            imageTag: imageTag
          )
          serviceDeploy(
            project: project,
            imageTag: imageTag,
            currentEnv: currentEnv,
            namespace: namespace
          )
        }
      }
    }
  }
}

@Library('jenkins-share-library') _

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
      yamlFile 'kubernetes/kubernetesPod.yaml'
    }
  }
  options {
    skipDefaultCheckout()
  }
  stages {

    stage('Build & Deploy') {

      agent { label 'gradle' }

      steps {
        script {

          stage('Checkout Code') {
            checkout scm
            gitCommit = getGitCommit()
            gitBranch = getGitBranch()
            imageTag = "${env.BUILD_ID}_${gitCommit}"
          }

          codeTest(
            gitCommit: gitCommit,
            gitBranch: gitBranch
          )

          buildArtifacts()

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

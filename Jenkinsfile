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
      yamlFile libraryResource('com/jenkins/library/build-pod.yaml')
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
            gitBranch = getGitBranch()
            imageTag = "${env.BUILD_ID}_${gitCommit}"
          }
        }
        container('gradle'){
          script {
            javaCodeTest(
              gitCommit: gitCommit,
              gitBranch: gitBranch
            )
            javaBuildArtifacts()
          }
        }
        container('docker') {
          script {
            publishArtifacts(
              imageTag: imageTag
            )
          }
        }
        container('helm') {
          script {
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
}

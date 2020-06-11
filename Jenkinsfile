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
    stage('Checkout Code') {
      steps {
        script {
          scm = checkout scm
          gitCommit = getGitCommit()
          gitBranch = getGitBranch()
          imageTag = "${env.BUILD_ID}_${gitCommit}"
        }
      }
    }

    stage('Test') {
      steps {
        container('gradle'){
          script {
            codeTest(
              gitCommit: gitCommit,
              gitBranch: gitBranch
            )
          }
        }
      }
    }

    stage('Build') {
      steps {
        container('gradle'){
          script {
            buildArtifacts()
          }
        }
      }
    }

    stage('publish') {
      steps {
        container('docker') {
          script {
            publishArtifacts(
              imageTag: imageTag
            )
          }
        }
      }
    }

    stage('deploy') {
      steps {
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

def gitCommit = null
def gitBranch = null
def imageTag = null
def podLabel = "worker-${UUID.randomUUID().toString()}"

pipeline {
  agent {
    kubernetes {
      cloud 'kubernetes-dev'
      label "${podLabel}"
      yamlFile 'kubernetes/kubernetesPod.yaml'
    }
  }
  environment {
    PROJECT='k8spipeline'
    NAMESPACE='devops'
    ENV='dev'
  }
  stages {
    stage('Prepare') {
      steps{
        script{
          def myRepo=checkout scm
          gitCommit = myRepo.GIT_COMMIT
          namespace = myRepo.GIT_BRANCH
          imageTag = "${env.BUILD_ID}_${gitCommit}"
        }
      }
    }
    stage('Test') {
      steps {
        container('gradle') {
          script {
            try {
              sh """
                pwd
                echo "GIT_BRANCH=${gitBranch}" >> /etc/environment
                echo "GIT_COMMIT=${gitCommit}" >> /etc/environment
                gradle test
                """
            } catch (Exception e) {
              echo 'Failed to test - ${currentBuild.fullDisplayName}'
              echo err.getMessage()
            }
          }
        }
      }
    }
    stage('Build') {
      steps {
        container('gradle') {
          sh "gradle build"
        }
      }
    }
    stage('publish') {
      steps {
        container('docker') {
          script {
            docker.withRegistry('https://752535683739.dkr.ecr.cn-northwest-1.amazonaws.com.cn/dev-repository',
            'ecr:cn-northwest-1:95189c1e-6db8-4c81-8e93-3e303e665433') {
              def newApp = docker.build("k8spipeline:${imageTag}")
              newApp.push() // record this snapshot (optional)
              newApp.push 'latest'
            }
          }
        }
      }
    }
    stage('deploy') {
      steps {
        container('helm') {
          script {
            sh "helm upgrade --install ${env.PROJECT} ./${env.PROJECT} --set image.tag=${imageTag} -f ${env.PROJECT}/values-${env.ENV}.yaml --namespace ${env.NAMESPACE}"
          }
        }
      }
    }
  }
}

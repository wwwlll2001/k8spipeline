pipeline {
  parameters {
    string(name: 'project', defaultValue: 'k8spipeline', description: 'project name')
    string(name: 'namespace', defaultValue: 'devops', description: 'namespace')
    string(name: 'env', defaultValue: 'dev', description: 'env')
  }
  environment {
    MY_REPO=checkout scm
    GIT_COMMIT=MY_REPO.GIT_COMMIT
    GIT_BRANCH=MY_REPO.GIT_BRANCH
    IMAGE_TAG = ${env.BUILD_ID}_${GIT_COMMIT}
  }
  agent {
    kubernetes-dev {
      yaml """
apiVersion: v1
kind: Pod
metadata:
 label: worker-${random.uuid}
spec:
 securityContext:
   runAsUser: 0
 containers:
 - name: gradle
   image: gradle:6.3.0
   command:
   - cat
   tty: true
   mountPath:
   - name: volumegradle
     path: /tmp/jenkins/.gradle
 - name: docker
   image: docker
   command:
   - cat
   tty: true
   mountPath:
   - name: volumedocker
     path: /var/run/docker.sock
 - name: kubectl
   image: lachlanevenson/k8s-kubectl:v1.16.10
   command:
   - cat
   tty: true
 - name: helm
   image: lachlanevenson/k8s-helm:v3.1.2
   command:
   - cat
   tty: true
 volumes:
 - name: volumegradle
   hostPath:
     path: /tmp/jenkins/.gradle
 - name: volumedocker
   hostPath:
     path: /var/run/docker.sock
"""
    }
  }
  stages {
    stage('Test') {
      steps {
        container('gradle') {
          script {
            echo 'project: ${params.project}'
            echo 'namespace: ${params.namespace}'
            echo 'env: ${params.env}'
            echo 'MY_REPO: ${MY_REPO}'
            echo 'GIT_COMMIT: ${GIT_COMMIT}'
            echo 'GIT_BRANCH: ${GIT_BRANCH}'
            echo 'IMAGE_TAG: ${IMAGE_TAG}'
            try {
              sh """
                pwd
                echo "GIT_BRANCH=${GIT_BRANCH}" >> /etc/environment
                echo "GIT_COMMIT=${GIT_COMMIT}" >> /etc/environment
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
              def newApp = docker.build("k8spipeline:${IMAGE_TAG}")
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
            sh "helm upgrade --install ${params.project} ./${params.project} --set image.tag=${IMAGE_TAG} -f ${params.project}/values-${params.env}.yaml --namespace ${params.namespace}"
          }
        }
      }
    }
  }
}

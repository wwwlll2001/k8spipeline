def gitCommit = null
def gitBranch = null
def imageTag = null
def podLabel = "worker-${UUID.randomUUID().toString()}"

pipeline {
  agent {
    kubernetes("kubernetes-dev") {
      label podLabel
      yaml """
        apiVersion: v1
        kind: Pod
          metadata:
            label: label-kubernetes
          spec:
            securityContext:
              runAsUser: 0
            containers:
            - name: gradle
              image: gradle:6.3.0
              imagePullPolicy: "IfNotPresent"
              command:
              - cat
              securityContext:
                privileged: false
              tty: true
              volumeMounts:
              - name: volume-0
                mountPath: /home/gradle/.gradle
                readOnly: false
              - mountPath: "/var/run/docker.sock"
                name: "volume-1"
                readOnly: false
              - mountPath: "/home/jenkins/agent"
                name: "workspace-volume"
                readOnly: false
            - name: docker
              image: docker
              imagePullPolicy: "IfNotPresent"
              command:
              - cat
              securityContext:
                privileged: false
              tty: true
              volumeMounts:
              - mountPath: "/home/gradle/.gradle"
                name: "volume-0"
                readOnly: false
              - mountPath: "/var/run/docker.sock"
                name: "volume-1"
                readOnly: false
              - mountPath: "/home/jenkins/agent"
                name: "workspace-volume"
                readOnly: false
            - name: kubectl
              image: lachlanevenson/k8s-kubectl:v1.16.10
              imagePullPolicy: "IfNotPresent"
              command:
              - cat
              securityContext:
                privileged: false
              tty: true
              volumeMounts:
              - mountPath: "/home/gradle/.gradle"
                name: "volume-0"
                readOnly: false
              - mountPath: "/var/run/docker.sock"
                name: "volume-1"
                readOnly: false
              - mountPath: "/home/jenkins/agent"
                name: "workspace-volume"
                readOnly: false
            - name: helm
              image: lachlanevenson/k8s-helm:v3.1.2
              imagePullPolicy: "IfNotPresent"
              command:
              - cat
              securityContext:
                privileged: false
              tty: true
              volumeMounts:
              - mountPath: "/home/gradle/.gradle"
                name: "volume-0"
                readOnly: false
              - mountPath: "/var/run/docker.sock"
                name: "volume-1"
                readOnly: false
              - mountPath: "/home/jenkins/agent"
                name: "workspace-volume"
                readOnly: false
            volumes:
              - hostPath:
                  path: "/tmp/jenkins/.gradle"
                name: "volume-0"
              - hostPath:
                  path: "/var/run/docker.sock"
                name: "volume-1"
              - emptyDir:
                  medium: ""
                name: "workspace-volume"
          """
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

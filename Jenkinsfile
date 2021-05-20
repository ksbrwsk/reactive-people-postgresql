node {
    agent {
                docker {
                    reuseNode true
                    image 'openjdk:11.0-jdk-slim'
                    args  '-v /var/run/docker.sock:/var/run/docker.sock --group-add 992'
                }
            }


    stage 'Clone the project'
    git 'https://github.com/ksbrwsk/reactive-talk-202012.git'

    stage("Compilation and Analysis") {
      parallel 'Compilation': {
        sh "./mvnw clean install -DskipTests"
      }
    }

    stage("Tests and Deployment") {
              parallel 'Unit tests': {
                stage("Running unit tests") {
                  sh "./mvnw test"
                }
              }
    }
}
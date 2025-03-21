pipeline {
    agent any

    environment {
        SERVICE_NAME = "member"
        IMAGE_NAME = "member-miyembro"
        IMAGE_TAG = "${IMAGE_NAME}:${BUILD_NUMBER}"
        REPOSITORY_TAG = "${DOCKERHUB_USERNAME}/${IMAGE_TAG}"
        DOCKER_HUB_CREDS = credentials('miyembro-docker-token')
    }

    stages {
        stage('Preparation') {
            steps {
                cleanWs()  // Clean the workspace
                git credentialsId: 'GitHub', url: "https://github.com/${ORGANIZATION_NAME}/${SERVICE_NAME}", branch: 'main'  // Clone the repository
                sh 'chmod +x gradlew'
            }
        }

        stage('Debug Workspace') {
            steps {
                sh 'pwd'  // Print current working directory
                sh 'ls -la'  // List files in the workspace
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build'  // Build the project using Gradle
            }
        }

        stage('Build and Push Image') {
            steps {
                script {
                    echo "REPOSITORY_TAG: ${REPOSITORY_TAG}"
                    echo "IMAGE_TAG: ${IMAGE_TAG}"
                    echo "IMAGE_NAME: ${IMAGE_NAME}"

                    // Authenticate with Docker Hub using Buildah
                    sh "buildah login -u ${DOCKER_HUB_CREDS_USR} -p ${DOCKER_HUB_CREDS_PSW} docker.io"

                    // Build the container image using Buildah
                    sh "buildah bud -t ${IMAGE_NAME} ."

                    // Tag the container image for the repository
                    sh "buildah tag ${IMAGE_NAME} ${REPOSITORY_TAG}"

                    // Push the container image to Docker Hub
                    sh "buildah push ${REPOSITORY_TAG}"
                }
            }
        }

        stage('Deploy to Cluster') {
            steps {
                sh 'envsubst < ${WORKSPACE}/deploy.yaml | kubectl apply -f -'  // Deploy to Kubernetes
            }
        }
    }

    post {
        always {
            cleanWs()  // Clean the workspace
        }
        success {
            echo "Pipeline succeeded!"
        }
        failure {
            echo "Pipeline failed!"
        }
    }
}
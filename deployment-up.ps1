#TODO: docker login when we are not already logged in on a remote server

docker build -t musicupload-spring-app ./backend

docker tag musicupload-spring-app:latest kovacsbotondjanos/musicupload-spring-app:latest

docker push kovacsbotondjanos/musicupload-spring-app:latest

docker build -t musicupload-react-app ./frontend

docker tag musicupload-react-app:latest kovacsbotondjanos/musicupload-react-app:latest

docker push kovacsbotondjanos/musicupload-react-app:latest

minikube start --driver=docker

kubectl apply -f kubernetes-deplyment/music-upload-mysql-db-deployment.yaml
kubectl apply -f kubernetes-deplyment/music-upload-minio-deployment.yaml
kubectl apply -f kubernetes-deplyment/music-upload-backend-deployment.yaml
kubectl apply -f kubernetes-deplyment/music-upload-frontend-deployment.yaml
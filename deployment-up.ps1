#TODO: docker login when we are not already logged in on a remote server

docker build -t kovacsbotondjanos/musicupload-spring-app:latest ./backend
docker push kovacsbotondjanos/musicupload-spring-app:latest

docker build -t kovacsbotondjanos/musicupload-recommendation-engine:latest ./backend-recommendation-engine
docker push kovacsbotondjanos/musicupload-recommendation-engine:latest

docker build -t kovacsbotondjanos/musicupload-react-app:latest ./frontend
docker push kovacsbotondjanos/musicupload-react-app:latest

minikube start --driver=docker

kubectl apply -f kubernetes-deplyment/music-upload-mysql-db-deployment.yaml
kubectl apply -f kubernetes-deplyment/music-upload-minio-deployment.yaml
kubectl apply -f kubernetes-deplyment/music-upload-backend-deployment.yaml
kubectl apply -f kubernetes-deplyment/music-upload-backend-recommendation-engine.yaml
kubectl apply -f kubernetes-deplyment/music-upload-frontend-deployment.yaml

minikube tunnel
Stop-Job -Name "MinikubeTunnel"
Remove-Job -Name "MinikubeTunnel"
Stop-Job -Name "FrontendPortForward"
Remove-Job -Name "FrontendPortForward"

kubectl delete -f kubernetes-deployment/music-upload-mysql-db-deployment.yaml
kubectl delete -f kubernetes-deployment/music-upload-minio-deployment.yaml
kubectl delete -f kubernetes-deployment/music-upload-backend-recommendation-engine.yaml
kubectl delete -f kubernetes-deployment/music-upload-backend-deployment.yaml
kubectl delete -f kubernetes-deployment/music-upload-frontend-deployment.yaml
kubectl delete -f kubernetes-deployment/music-upload-redis-deployment.yaml

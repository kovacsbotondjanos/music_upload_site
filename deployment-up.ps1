docker image rm kovacsbotondjanos/musicupload-recommendation-engine:latest
docker image rm kovacsbotondjanos/musicupload-spring-app:latest
docker image rm kovacsbotondjanos/musicupload-react-app:latest

docker build --no-cache -t kovacsbotondjanos/musicupload-spring-app:latest ./backend
docker push kovacsbotondjanos/musicupload-spring-app:latest

docker build --no-cache -t kovacsbotondjanos/musicupload-react-app:latest ./frontend
docker push kovacsbotondjanos/musicupload-react-app:latest

docker build --no-cache -t kovacsbotondjanos/musicupload-recommendation-engine:latest ./backend-recommendation-engine
docker push kovacsbotondjanos/musicupload-recommendation-engine:latest

minikube start --driver=docker --cpus=8 --memory=7000 --disk-size=50g

kubectl apply -f kubernetes-deployment/music-upload-mysql-db-deployment.yaml
kubectl apply -f kubernetes-deployment/music-upload-minio-deployment.yaml
kubectl apply -f kubernetes-deployment/music-upload-backend-recommendation-engine.yaml
kubectl apply -f kubernetes-deployment/music-upload-backend-deployment.yaml
kubectl apply -f kubernetes-deployment/music-upload-frontend-deployment.yaml
kubectl apply -f kubernetes-deployment/music-upload-redis-deployment.yaml

do {
    Clear-Host
    $statuses = kubectl get pods -o jsonpath="{range .items[*]}{.metadata.name}={.status.phase}{'\n'}{end}"
    Write-Output "Current pod statuses:"
    $statusLines = $statuses -split "\\n"
    $statusLines | ForEach-Object { Write-Output $_ }
    $allRunning = $true
    foreach ($status in $statusLines) {
        if ($status -match "=") {
            $phase = $status.Split("=")[1]
            if ($phase -ne "Running") {
                $allRunning = $false
            }
        }
    }
    if (-not $allRunning) {
        Start-Sleep -Seconds 1
    }
} until ($allRunning)

Write-Output "All pods are Running!"

Start-Job -Name "MinikubeTunnel" -ScriptBlock { minikube tunnel }

Start-Job -Name "FrontendPortForward" -ScriptBlock { kubectl port-forward service/react-frontend-service 30001:80 }

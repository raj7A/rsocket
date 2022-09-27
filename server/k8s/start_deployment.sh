kubectl create namespace non-prod
kubectl create configmap server-config --from-file=application.properties -n non-prod
kubectl create -f AppDeploy.yml -n non-prod
sleep 20
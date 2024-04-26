# 가동중인 awsstudy 도커 중단 및 삭제
sudo docker ps -a -q --filter "name=savetime" | grep -q . && docker stop savetime && docker rm -f savetime | true

# 기존 이미지 삭제
sudo docker rmi -f rhauddn111/save-time | true
#sudo docker images | grep "rhauddn111/helper" && docker rmi rhauddn111/helper || echo "Image not found. Skipping removal."


# 도커허브 이미지 pull
sudo docker pull rhauddn111/save-time

# 도커 run -d를 붙여야만 백그라운드로 실행이 된다 주의할것!! -d를 안붙이면 포그라운드에서 실행되서 스프링 프로젝트가 계속 보이며 젠킨스가 마무리 되지 못한다
#docker run -d -p 8080:8080 --name helper rhauddn111/helper
sudo docker run -d --network host --name savetime -p 443:443 -t rhauddn111/save-time

# 사용하지 않는 불필요한 이미지 삭제 -> 현재 컨테이너가 물고 있는 이미지는 삭제되지 않습니다.
#docker rmi -f $(docker images -f "dangling=true" -q) || true
sudo docker rmi -f rhauddn111/save-time | true
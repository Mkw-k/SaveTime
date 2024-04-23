# 가동중인 awsstudy 도커 중단 및 삭제
sudo docker ps -a -q --filter "name=savetime" | grep -q . && docker stop savetime && docker rm -f savetime | true

# 도커 run -d를 붙여야만 백그라운드로 실행이 된다 주의할것!! -d를 안붙이면 포그라운드에서 실행되서 스프링 프로젝트가 계속 보이며 젠킨스가 마무리 되지 못한다
#docker run -d -p 8080:8080 --name savetime rhauddn111/savetime
sudo docker run -d --network host --name savetime -p 8080:8080 -t rhauddn111/savetime


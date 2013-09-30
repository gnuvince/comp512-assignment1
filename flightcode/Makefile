policy:
	@echo "grant codeBase \"file:$(CURDIR)\" {" > java.policy
	@echo "    permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

compileflight: policy
	mkdir -p bin
	javac -d bin -cp ../servercode:../tcpcode/bin ResImpl/*.java

runflight: compileflight
	java -cp . -Djava.security.policy=./java.policy -Djava.rmi.server.codebase=file:$(CURDIR) ResImpl.FlightManagerImpl localhost 5007

clean:
	rm -rf bin/*

policy:
	@echo "grant codeBase \"file:$(CURDIR)\" {" > java.policy
	@echo "    permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

compilehotel: policy
	mkdir -p bin
	javac -d bin -cp ../servercode/bin:../tcpcode/bin ResImpl/*.java

runhotel: compilehotel
	java -cp . -Djava.security.policy=./java.policy -Djava.rmi.server.codebase=file:$(CURDIR) ResImpl.HotelManagerImpl

clean:
	rm -rf bin/*

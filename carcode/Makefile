policy:
	@echo "grant codeBase \"file:$(CURDIR)\" {" > java.policy
	@echo "    permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

compilecar: policy
	mkdir -p bin
	javac -d bin -cp ../servercode:../tcpcode/bin ResImpl/*.java

runcar: compilecar
	java -cp . -Djava.security.policy=./java.policy -Djava.rmi.server.codebase=file:$(CURDIR) ResImpl.CarManagerImpl localhost 5006

clean:
	rm -rf bin/*

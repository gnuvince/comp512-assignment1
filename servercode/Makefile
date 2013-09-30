policy:
	@echo "grant codeBase \"file:$(CURDIR)\" {" > java.policy
	@echo "    permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

compileserver: policy
	mkdir -p bin
	javac -d bin -cp . ResInterface/ResourceManager.java
	jar cvf ResInterface.jar bin/ResInterface/*.class
	javac -d bin -cp . ResImpl/*.java

runserver: compileserver
	java -cp . -Djava.security.policy=./java.policy -Djava.rmi.server.codebase=file:$(CURDIR) ResImpl.ResourceManagerImpl lab7-21.cs.mcgill.ca

clean:
	rm -rf bin/*
	rm -f ResInterface.jar

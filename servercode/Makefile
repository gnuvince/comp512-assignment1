policy:
	@echo "grant codeBase \"file:$(CURDIR)\" {" > java.policy
	@echo "    permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

compileserver: policy
	javac -cp . ResInterface/ResourceManager.java
	jar cvf ResInterface.jar ResInterface/*.class
	javac -g -cp . ResImpl/*.java

runserver: compileserver
	java -cp . -Djava.security.policy=./java.policy -Djava.rmi.server.codebase=file:$(CURDIR) ResImpl.ResourceManagerImpl lab7-21.cs.mcgill.ca

clean:
	rm -f ResImpl/*.class
	rm -f ResInterface/*.class
	rm -f ResInterface.jar

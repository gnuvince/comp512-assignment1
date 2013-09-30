policy:
	@echo "grant codeBase \"file:$(CURDIR)\" {" > java.policy
	@echo "    permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

compileflight: policy
	javac -cp . ResInterface/ItemManager.java
	jar cvf ResInterface.jar ResInterface/*.class
	javac -cp . ResImpl/*.java

runflight: compileflight
	java -cp . -Djava.security.policy=./java.policy -Djava.rmi.server.codebase=file:$(CURDIR) ResImpl.FlightManagerImpl localhost 5007

clean:
	rm -f ResImpl/*.class
	rm -f ResInterface/*.class
	rm -f ResInterface.jar

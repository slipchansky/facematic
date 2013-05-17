cd facematic
call mvn clean install
call mvn -DaltDeploymentRepository=snapshot-repo::default::file:../repo/snapshots clean deploy
del ../../src/main/webapp/schema/facematic.xsd
copy src/main/resources/schema/facematic.xsd  ../../src/main/webapp/schema/facematic.xsd

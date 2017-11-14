maintainer := "Crayondata"
name := """TasteBot"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.38"
libraryDependencies += "org.hibernate" % "hibernate-core" % "3.6.10.Final"
libraryDependencies += "org.json" % "json" % "20160212"
libraryDependencies += "com.sun.jersey" % "jersey-bundle" % "1.18.5"
libraryDependencies += "com.sun.jersey" % "jersey-client" % "1.18.5"
libraryDependencies += "com.sun.jersey" % "jersey-json" % "1.18.5"
libraryDependencies += "com.google.code.gson" % "gson" % "2.7"
libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.2"
libraryDependencies += "org.apache.httpcomponents" % "httpcore" % "4.4.5"
libraryDependencies += "com.cloudinary" % "cloudinary-http44" % "1.5.0"
libraryDependencies += "javax.mail" % "mail" % "1.4.7"

dockerExposedPorts in Docker := Seq(9000, 9443)


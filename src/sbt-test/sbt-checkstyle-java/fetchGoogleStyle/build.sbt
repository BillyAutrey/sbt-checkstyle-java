val checkstyleTest = taskKey[Unit]("Executes checkstyle")

lazy val root = (project in file(".")).
  settings(
    version := "0.1",
    name := "checkstyleFetchTest",
    checkstyleTest := {
        val result = checkstyle.value
        assert (result == 0)
    }
  )
#!/bin/bash

java -Dfile.encoding=ISO-8859-1 -cp cfe-exam-agent-@version@.jar -Xms128m -Xmx4096m financial.fraud.cfe.agent.CFEExamAgent


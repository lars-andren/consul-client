``` java
se.cloudcharge.consul.ConsulConnector consulConnector = se.cloudcharge.consul.ConsulConnector.builder()
            .withUrl(CONSUL_URL_WITH_PORT)
            .build();

se.cloudcharge.consul.AgentClient agentClient = consulConnector.getAgentClient();

Check check = Check.builder()
        .ttl("10s")
        .id(CHECK_ID)
        .build();

Service service = Service.builder()
       .id(SERVICE_ID)
       .port(1)
       .name(SERVICE_NAME)
       .check(check)
       .build();

agentClient.register(service);
```
package com.example.JavaFileCopier;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.stereotype.Component;

@Component
public class MyRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("file:F:/data/Splitter1/input?noop=true")
		.split(body().tokenize(",")).streaming().wireTap("direct:wiretap")
		.to("jms:topic:testing");
		from("jms:topic:testing").to("jms:validate1?exchangePattern=InOut").to("direct:one");
		from("jms:topic:testing").to("jms:validate2?exchangePattern=InOut").to("direct:two");
		from("direct:one").to("stream:out");
		from("direct:two").to("stream:out");
		from("jms:validate1").process(new Processor() {
			
			public void process(Exchange exchange) throws Exception {
				System.out.println("Waiting on Validate1");
				Thread.sleep(6000);
				System.out.println("done with Validate1");
				
			}
		});
		from("jms:validate2").process(new Processor() {
			
			public void process(Exchange exchange) throws Exception {
				System.out.println("Waiting on Validate2");
				Thread.sleep(10000);
				System.out.println("done with Validate2");
				
			}
		});
	}
	
	//from("jms:incomingOrders").inOut().to("jms:validate")
	//from("jms:incomingOrders").inOut("jms:validate")
}

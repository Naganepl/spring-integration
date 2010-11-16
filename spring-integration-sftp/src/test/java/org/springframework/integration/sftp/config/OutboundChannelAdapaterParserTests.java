/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.integration.sftp.config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.integration.endpoint.EventDrivenConsumer;
import org.springframework.integration.sftp.outbound.SftpSendingMessageHandler;
import org.springframework.integration.sftp.session.QueuedSftpSessionPool;
import org.springframework.integration.sftp.session.SftpSessionFactory;
import org.springframework.integration.test.util.TestUtils;

/**
 * @author Oleg Zhurakousky
 *
 */
public class OutboundChannelAdapaterParserTests {

	@Test
	public void testOutboundChannelAdapaterWithId(){
		ApplicationContext context = 
			new ClassPathXmlApplicationContext("OutboundChannelAdapaterParserTests-context.xml", this.getClass());
		Object consumer = context.getBean("sftpOutboundAdapter");
		assertTrue(consumer instanceof EventDrivenConsumer);
		assertEquals(context.getBean("inputChannel"), TestUtils.getPropertyValue(consumer, "inputChannel"));
		assertEquals("sftpOutboundAdapter", ((EventDrivenConsumer)consumer).getComponentName());
		SftpSendingMessageHandler handler = (SftpSendingMessageHandler) TestUtils.getPropertyValue(consumer, "handler");
		Expression remoteDirectoryExpression = (Expression) TestUtils.getPropertyValue(handler, "remoteDirectoryExpression");
		assertNotNull(remoteDirectoryExpression);
		assertTrue(remoteDirectoryExpression instanceof LiteralExpression);
		assertEquals(context.getBean("fileNameGenerator"), TestUtils.getPropertyValue(handler, "fileNameGenerator"));
		assertEquals("UTF-8", TestUtils.getPropertyValue(handler, "charset"));
		assertNotNull(TestUtils.getPropertyValue(handler, "temporaryBufferFolder"));
		assertNotNull(TestUtils.getPropertyValue(handler, "temporaryBufferFolderFile"));
		QueuedSftpSessionPool clientPoll = (QueuedSftpSessionPool) TestUtils.getPropertyValue(handler, "pool");
		SftpSessionFactory clientFactory = (SftpSessionFactory) TestUtils.getPropertyValue(clientPoll, "sftpSessionFactory");
		assertEquals("localhost", TestUtils.getPropertyValue(clientFactory, "host"));
		assertEquals(2222, TestUtils.getPropertyValue(clientFactory, "port"));
	}
	
	@Test
	public void testOutboundChannelAdapaterWithWithRemoteDirectoryExopression(){
		ApplicationContext context = 
			new ClassPathXmlApplicationContext("OutboundChannelAdapaterParserTests-context.xml", this.getClass());
		Object consumer = context.getBean("sftpOutboundAdapterWithExpression");
		assertTrue(consumer instanceof EventDrivenConsumer);
		assertEquals(context.getBean("inputChannel"), TestUtils.getPropertyValue(consumer, "inputChannel"));
		assertEquals("sftpOutboundAdapterWithExpression", ((EventDrivenConsumer)consumer).getComponentName());
		SftpSendingMessageHandler handler = (SftpSendingMessageHandler) TestUtils.getPropertyValue(consumer, "handler");
		SpelExpression remoteDirectoryExpression = (SpelExpression) TestUtils.getPropertyValue(handler, "remoteDirectoryExpression");
		assertNotNull(remoteDirectoryExpression);
		assertEquals("'foo' + '/' + 'bar'", remoteDirectoryExpression.getExpressionString());
		assertEquals(context.getBean("fileNameGenerator"), TestUtils.getPropertyValue(handler, "fileNameGenerator"));
		assertEquals("UTF-8", TestUtils.getPropertyValue(handler, "charset"));
		assertNotNull(TestUtils.getPropertyValue(handler, "temporaryBufferFolder"));
		assertNotNull(TestUtils.getPropertyValue(handler, "temporaryBufferFolderFile"));
		
	}
}

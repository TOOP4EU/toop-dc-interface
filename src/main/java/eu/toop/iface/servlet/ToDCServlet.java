/**
 * Copyright (C) 2018 toop.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.toop.iface.servlet;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.toop.commons.dataexchange.TDETOOPDataResponseType;
import eu.toop.commons.exchange.ToopMessageBuilder;
import eu.toop.iface.IToopInterfaceDC;
import eu.toop.iface.ToopInterfaceManager;

/**
 * This servlet can be included in Java DC implementations to receive messages
 * from the MP (step 4/4). It should read the received ASiC container and
 * extract the {@link TDETOOPDataResponseType} object. This is than forwarded to
 * the {@link IToopInterfaceDC} implementation registered in
 * {@link ToopInterfaceManager}.
 *
 * @author Philip Helger
 */
@WebServlet ("/to-dc")
public class ToDCServlet extends HttpServlet {
  private static final Logger s_aLogger = LoggerFactory.getLogger (ToDCServlet.class);

  @Override
  protected void doPost (@Nonnull final HttpServletRequest aHttpServletRequest,
                         @Nonnull final HttpServletResponse aHttpServletResponse) throws ServletException, IOException {
    // Parse ASiC
    final TDETOOPDataResponseType aResponseMsg = ToopMessageBuilder.parseResponseMessage (aHttpServletRequest.getInputStream ());
    if (aResponseMsg == null) {
      // The message content is invalid
      s_aLogger.error ("The request does not contain an ASiC archive or the ASiC archive does not contain a TOOP Response Message!");
      aHttpServletResponse.setStatus (HttpServletResponse.SC_BAD_REQUEST);
    } else {
      // Call callback
      ToopInterfaceManager.getInterfaceDC ().onToopResponse (aResponseMsg);

      // Done - no content
      aHttpServletResponse.setStatus (HttpServletResponse.SC_NO_CONTENT);
    }
  }
}
package br.com.caelum.vraptor.observer.download;

import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.View;

/**
 * @author Rodrigo Turini
 * @author Victor Kendy Harada
 */
public class DownloadView implements View {

	private HttpServletResponse response;

	/**
	 * @deprecated CDI eyes only
	 */
	protected DownloadView() {
	}

	@Inject
	public DownloadView(HttpServletResponse response){
		this.response = response;
	}

	public void of(Download download) throws IOException {
		OutputStream output = response.getOutputStream();
		download.write(response);
		output.flush();
	}
}
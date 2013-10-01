package br.com.caelum.vraptor.musicjungle.dao.repository;

import java.util.List;

import br.com.caelum.vraptor.musicjungle.model.Music;

public interface IMusicRepository {
	public Music add(Music music);
	public Music load(Music music);
	public List<Music> listAll();
	public List<Music> searchSimilarTitle(String title);
}

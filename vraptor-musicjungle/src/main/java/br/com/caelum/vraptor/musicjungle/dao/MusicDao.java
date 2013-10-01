package br.com.caelum.vraptor.musicjungle.dao;

import java.util.List;

import br.com.caelum.vraptor.musicjungle.dao.repository.Playlist;
import br.com.caelum.vraptor.musicjungle.model.Music;

public class MusicDao extends GenericJPADao<Music> implements Playlist {

	public MusicDao() {
		super(Music.class);
	}

	@Override
	public Music add(Music music) {
		return persist(music);
	}
	
	@Override
	public Music load(Music music) {
		return findByPK(music.getId());
	}

	@Override
	public List<Music> listAll() {
		return findAll();
	}
	
	@Override
	public List<Music> searchSimilarTitle(String title) {
		return findByPredicate(builder.like(builder.upper(from.<String>get("title")), "%" + title + "%"));
	}
}

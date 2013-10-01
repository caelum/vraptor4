package br.com.caelum.vraptor.musicjungle.dao;

import br.com.caelum.vraptor.musicjungle.dao.repository.OwnersMusic;
import br.com.caelum.vraptor.musicjungle.model.MusicOwner;

public class MusicOwnerDao extends GenericJPADao<MusicOwner> implements OwnersMusic {

	public MusicOwnerDao() {
		super(MusicOwner.class);
	}

	@Override
	public MusicOwner add(MusicOwner musicOwner) {
		return persist(musicOwner);
	}
}

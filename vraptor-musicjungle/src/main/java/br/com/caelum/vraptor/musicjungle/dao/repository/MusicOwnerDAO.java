package br.com.caelum.vraptor.musicjungle.dao.repository;

import br.com.caelum.vraptor.musicjungle.dao.GenericDAO;
import br.com.caelum.vraptor.musicjungle.model.MusicOwner;

public class MusicOwnerDAO extends GenericDAO<MusicOwner> implements IMusicOwnerRepository {

	public MusicOwnerDAO() {
		super(MusicOwner.class);
	}

	@Override
	public MusicOwner add(MusicOwner musicOwner) {
		return persist(musicOwner);
	}
}

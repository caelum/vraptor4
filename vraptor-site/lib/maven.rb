require 'httparty'

class Maven

  include HTTParty

  base_uri 'http://search.maven.org'
  default_params wt: 'json'
  headers 'Accept' => 'application/json'
  format :json

  def self.latest_version(group_id, artifact_id)

    resp = get('/solrsearch/select', query: {
      q: %{g:"#{group_id}" AND a:"#{artifact_id}"}, rows: 1
    })
    raise HTTParty::ResponseError.new(resp) if resp.code != 200

    doc = resp.parsed_response['response']
    if doc['numFound'] > 0
      doc['docs'][0]['latestVersion']
    else
      'LATEST_VERSION'
    end
  end

  def self.vraptor_version
  	self.latest_version('br.com.caelum', 'vraptor')
  end

  def self.plugin_version(artifact_id)
    self.latest_version('br.com.caelum.vraptor', artifact_id)
  end
  
end
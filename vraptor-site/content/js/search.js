/* adapted from compass-style.org */
function unique(arrayName) {
  var newArray = new Array();
  label: for (var i = 0; i < arrayName.length; i++) {
    for (var j = 0; j < newArray.length; j++) {
      if (newArray[j] == arrayName[i]) {
        continue label;
      }
    }
    newArray[newArray.length] = arrayName[i];
  }
  return newArray;
}
function search(query, callback) {
  var terms = query.replace(/[\W\s_]+/m,' ').toLowerCase().split(/\s+/);
  var matching_ids = null;
  for (var i = 0; i < terms.length; i++) {
    var term = terms[i];
    var exactmatch = index.terms[term] || [];
    var approxmatch = index.approximate[term] || [];
    var ids = unique(exactmatch.concat(approxmatch));
    if (matching_ids) {
      matching_ids = matching_ids.filter(function(id) {
        return ids.indexOf(id) != -1;
      });
    } else {
      matching_ids = ids;
    }
  }
  callback(matching_ids.map(function(id){ return index.items[id]; }))
}

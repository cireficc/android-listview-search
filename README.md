# Android ListView Search

Search an Android ListView using a custom filter on objects that make up the ListView.

In this example, we are searching a list of `Word` that have an `original` field (sometimes containing non-ASCII characters),
and a `normalized` field (the ASCII-only version of the string). We then use a custom filter to search on both of those fields,
depending on whether or not the search term contained non-ASCII characters.


## TODO
An improvement to the search would be to always normalize the search term, then use that to filter on the `normalized`,
never filtering on the `original`.

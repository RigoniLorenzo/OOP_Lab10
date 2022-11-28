package it.unibo.oop.lab.streams;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs
            .stream() //stream with songs
            .map(sN -> sN.getSongName()) //stream with name of songs
            .sorted(); //sorting songs
    }

    @Override
    public Stream<String> albumNames() {
        return this.albums
            .keySet() //get all albums names
            .stream(); //put in a stream
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return this.albums
            .entrySet() //get all year
            .stream() //put in a stream
            .filter(y -> y.getValue() == year) //filter by year
            .map(y -> y.getKey()); //get albums names
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) this.songs
            .stream() //stream with all songs
            .filter(a -> a.getAlbumName().isPresent()) //filter if album is present
            .filter(a -> a.getAlbumName().get().equals(albumName)) //filter album by name
            .count(); //count the songs of album
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) this.songs
            .stream() //stream with all songs
            .filter(a -> a.getAlbumName().isEmpty()) //filter if album isn't present
            .count(); //count the songs that aren't present
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return this.songs
            .stream() //stream with all songs
            .filter(a -> a.getAlbumName().isPresent()) //filter if album is present
            .filter(a -> a.getAlbumName().get().equals(albumName)) //filter album by name
            .mapToDouble(s -> s.getDuration()) //get all duration of songs
            .average(); //get average
    }

    @Override
    public Optional<String> longestSong() {
        return this.songs
            .stream() //stream with all songs
            .collect(Collectors.maxBy((s1, s2) -> Double.compare(s1.getDuration(), s2.getDuration())))
            //get the max duration song
            .map(s -> s.getSongName()); //get the song name
    }

    @Override
    public Optional<String> longestAlbum() {
        return this.songs
            .stream() //stream with all songs
            .filter(a -> a.getAlbumName().isPresent()) //filter if album exist
            .collect(Collectors.groupingBy(a -> a.getAlbumName(), Collectors.summingDouble(s -> s.getDuration())))
            //grouping by the sum of duration
            .entrySet() //get all the values
            .stream() //put in a stream
            .collect(Collectors.maxBy((e1, e2) -> Double.compare(e1.getValue(), e2.getValue())))
            //get the max entry
            .flatMap(e -> e.getKey()); //get the album name
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}

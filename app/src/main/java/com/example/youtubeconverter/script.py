from pytube import YouTube
import os
# import eyed3

def converter(url: str, destination: str):
    # url input from user
    yt = YouTube(url)

    # extract only audio
    video = yt.streams.get_audio_only()

    # download the file
    out_file = video.download(output_path=destination)

    # save the file
    base, ext = os.path.splitext(out_file)
    new_file = base + '.mp3'
    os.rename(out_file, new_file)

#     mp3File = eyed3.load(new_file)
#     mp3File.tag.artist = yt.artist
#     mp3File.tag.album = yt.album
#     mp3File.tag.album_artist = yt.album_artist

#     mp3File.tag.save()

    # result of success
    return yt.title + " has been successfully downloaded."
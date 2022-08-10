from pytube import YouTube
import os

def converter(url: str, destination: str):
    # url input from user
    yt = YouTube(url)

    # extract only audio
    video = yt.streams.filter(only_audio=True).first()

    # download the file
    out_file = video.download(output_path=destination)

    # save the file
    base, ext = os.path.splitext(out_file)
    new_file = base + '.mp3'
    os.rename(out_file, new_file)

    # result of success
    return yt.title + " has been successfully downloaded."
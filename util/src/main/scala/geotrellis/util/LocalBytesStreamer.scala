package geotrellis.util

import java.io._
import java.nio.channels.FileChannel
import java.nio.channels.FileChannel.MapMode._

class LocalBytesStreamer(path: String, val chunkSize: Int) extends BytesStreamer {
  private val f: File = new File(path)
  private val inputStream: FileInputStream = new FileInputStream(f)
  private val channel: FileChannel =  inputStream.getChannel

  def objectLength: Long = channel.size

  def getArray(start: Long, length: Long): Array[Byte] = {
    val chunk: Long =
      if (!passedLength(length + start))
        length + start
      else
        objectLength

    try {
      val byteBuffer = channel.map(READ_WRITE, start, chunk)
      byteBuffer.array
    } finally {
      channel.close()
      inputStream.close()
    }
  }
}

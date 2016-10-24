package geotrellis.util

import java.io._
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.FileChannel.MapMode._

class LocalBytesStreamer(path: String, val chunkSize: Int) extends BytesStreamer {
  private val f: File = new File(path)
  private val inputStream: FileInputStream = new FileInputStream(f)
  private val channel: FileChannel =  inputStream.getChannel

  def objectLength: Long = channel.size

  def getArray(start: Long, length: Long): Array[Byte] = {
    println("I need to access the stream")
    val chunk: Long =
      if (!passedLength(length + start))
        length
      else
        objectLength - start

    println(start, length, chunk)

    val buffer = channel.map(READ_ONLY, start, chunk)
    var i = 0
    val data = Array.ofDim[Byte](buffer.capacity)
    while(buffer.hasRemaining()) {
      val n = math.min(buffer.remaining(), (1<<18))
      buffer.get(data, i, n)
      i += n
    }
    //channel.close()
    //inputStream.close()
    data
  }
}

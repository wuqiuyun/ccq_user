package ccj.yun28.com.utils;


import java.io.Closeable;
import java.io.IOException;
/**
 * 流的处理
 * @author Administrator
 *
 */
public class StreamUtils
{
    public static void close(Closeable closeable)
    {
        if (closeable != null)
        {
            try
            {
                closeable.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}


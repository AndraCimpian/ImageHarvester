package eu.europeana.harvester.cluster.slave.processing.thumbnail;

import eu.europeana.harvester.domain.MediaFile;
import eu.europeana.harvester.domain.ThumbnailType;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import static eu.europeana.harvester.TestUtils.*;
import static org.junit.Assert.*;

/**
 * Created by salexandru on 29.05.2015.
 */
public class ThumbnailGeneratorTest {
    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            System.out.println("Starting test: " + description.getMethodName());
        }
    };

    private boolean almostSameSize(final byte[] first,final byte[] second) {
        return (((first.length / second.length) > 0.95) || ((second.length / first.length) > 0.95));
    }

    @Test
    public void test_ThumbnailGeneration_Image1_Medium() throws Exception {
        final Integer width = ThumbnailType.MEDIUM.getWidth();
        final Integer height = ThumbnailType.MEDIUM.getHeight();

        final MediaFile thumbnail = new ThumbnailGenerator(PATH_COLORMAP)
                                            .createMediaFileWithThumbnail(height, width, "", getPath(Image1),
                                                                          imagesInBytes.get(Image1), getPath(Image1)
                                                                         );

        assertTrue(IMAGE_MIMETYPE.equalsIgnoreCase(thumbnail.getContentType()));
        assertEquals("", thumbnail.getSource());
        assertEquals(width, thumbnail.getSize());
        assertEquals(getPath(Image1), thumbnail.getOriginalUrl());
        assertEquals(Image1, thumbnail.getName());
        assertTrue(almostSameSize(imagesInBytes.get(Image1ThumbnailMedium), thumbnail.getContent()));
    }

    @Test
    public void test_ThumbnailGeneration_Image1_Large() throws Exception {
        final Integer width = ThumbnailType.LARGE.getWidth();
        final Integer height = ThumbnailType.LARGE.getHeight();

        final MediaFile thumbnail = new ThumbnailGenerator(PATH_COLORMAP)
                                            .createMediaFileWithThumbnail(height, width, "", getPath(Image1),
                                                                          imagesInBytes.get(Image1), getPath(Image1)
                                                                         );

        assertTrue(IMAGE_MIMETYPE.equalsIgnoreCase(thumbnail.getContentType()));
        assertEquals("", thumbnail.getSource());
        assertEquals(width, thumbnail.getSize());
        assertEquals(getPath(Image1), thumbnail.getOriginalUrl());
        assertEquals(Image1, thumbnail.getName());
        assertTrue(almostSameSize(imagesInBytes.get(Image1ThumbnailLarge), thumbnail.getContent()));
    }

    @Test
    public void test_ThumbnailGeneration_Image2_Medium() throws Exception {
        final Integer width = ThumbnailType.MEDIUM.getWidth();
        final Integer height = ThumbnailType.MEDIUM.getHeight();

        System.out.println(getPath(Image2));
        final MediaFile thumbnail = new ThumbnailGenerator(PATH_COLORMAP)
                                            .createMediaFileWithThumbnail(height, width, "", getPath(Image2),
                                                                          imagesInBytes.get(Image2), getPath(Image2)
                                                                         );

        assertTrue(IMAGE_MIMETYPE.equalsIgnoreCase(thumbnail.getContentType()));
        assertEquals("", thumbnail.getSource());
        assertEquals(width, thumbnail.getSize());
        assertEquals(getPath(Image2), thumbnail.getOriginalUrl());
        assertEquals(Image2, thumbnail.getName());
        assertTrue(almostSameSize(imagesInBytes.get(Image2ThumbnailMedium), thumbnail.getContent()));
    }

    @Test
    public void test_ThumbnailGeneration_Image2_Large() throws Exception {
        final Integer width = ThumbnailType.LARGE.getWidth();
        final Integer height = ThumbnailType.LARGE.getHeight();

        final MediaFile thumbnail = new ThumbnailGenerator(PATH_COLORMAP)
                                            .createMediaFileWithThumbnail(height, width, "", getPath(Image2),
                                                                          imagesInBytes.get(Image2), getPath(Image2)
                                                                         );

        assertTrue(IMAGE_MIMETYPE.equalsIgnoreCase(thumbnail.getContentType()));
        assertEquals("", thumbnail.getSource());
        assertEquals(width, thumbnail.getSize());
        assertEquals(getPath(Image2), thumbnail.getOriginalUrl());
        assertEquals(Image2, thumbnail.getName());
        assertTrue(almostSameSize(imagesInBytes.get(Image2ThumbnailLarge), thumbnail.getContent()));
    }

    @Test
    public void test_ThumbnailGeneration_Fail_Audio() throws Exception {
        final Integer width = ThumbnailType.LARGE.getWidth();
        final Integer height = ThumbnailType.LARGE.getHeight();

        final MediaFile thumbnail = new ThumbnailGenerator(PATH_COLORMAP).createMediaFileWithThumbnail(height, width,
                                                                                                       "",
                                                                                                       getPath(Audio2),
                                                                                                       imagesInBytes
                                                                                                               .get(Audio2),
                                                                                                       getPath(Audio2));
        assertNull(thumbnail);
    }

    @Test
    public void test_ThumbnailGeneration_Fail_Video() throws Exception {
        final Integer width = ThumbnailType.LARGE.getWidth();
        final Integer height = ThumbnailType.LARGE.getHeight();

        final MediaFile thumbnail = new ThumbnailGenerator(PATH_COLORMAP).createMediaFileWithThumbnail(height, width,
                                                                                                       "",
                                                                                                       getPath(Video2),
                                                                                                       imagesInBytes
                                                                                                               .get(Video2),
                                                                                                       getPath(Video2));

        assertNull(thumbnail);
    }

    @Test
    public void test_ThumbnailGeneration_Fail_Text() throws Exception {
        final Integer width = ThumbnailType.LARGE.getWidth();
        final Integer height = ThumbnailType.LARGE.getHeight();

        final MediaFile thumbnail = new ThumbnailGenerator(PATH_COLORMAP).createMediaFileWithThumbnail(height, width,
                                                                                                       "",
                                                                                                       getPath(Text2),
                                                                                                       imagesInBytes
                                                                                                               .get(Text2),
                                                                                                       getPath(Text2));

        assertNull(thumbnail);
    }
}

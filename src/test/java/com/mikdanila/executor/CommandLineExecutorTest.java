package com.mikdanila.executor;

import com.mikdanila.archiver.Archive;
import com.mikdanila.dearchiver.Unarchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Danila Mikhaylov on 17/02/2021
 */
@RunWith(MockitoJUnitRunner.class)
public class CommandLineExecutorTest {

    private CommandLineExecutor commandLineExecutor;

    @Mock
    private Archive archive;

    @Mock
    private Unarchive unarchive;

    @Before
    public void setUp(){
        commandLineExecutor = new CommandLineExecutor(archive, unarchive);
    }

    @Test
    public void shouldInitializeWithRightArguments() throws Exception {
        String[] args = {"Test.file", "OutputFile"};
        commandLineExecutor.run(args);
        verify(archive, times(1)).archive(anyList());
        verify(unarchive, never()).unarchive();
    }

    @Test
    public void shouldInitializeWithoutArguments() throws Exception {
        String[] args = {};
        commandLineExecutor.run(args);
        verify(unarchive, times(1)).unarchive();
        verify(archive, never()).archive(anyList());
    }

}